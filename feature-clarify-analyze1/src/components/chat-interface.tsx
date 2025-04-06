
import { useState, useRef, useEffect, useCallback } from "react";
import { requirementsApi } from "@/services/api";
import logger from "@/lib/logger";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Avatar } from "@/components/ui/avatar";
import { Card } from "@/components/ui/card";
import { Brain, Send, User } from "lucide-react";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import { LoadingScreen } from "@/components/loading-screen";

interface Message {
  id: string;
  sender: "user" | "ai";
  content: string;
  timestamp: Date;
}

interface ChatInterfaceProps {
  featureText: string;
  onComplete: () => void;
  onRequirementCreated?: (id: number) => void;
}

export function ChatInterface({ featureText, onComplete, onRequirementCreated }: ChatInterfaceProps) {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [isInitializing, setIsInitializing] = useState(true);
  const [isCompleting, setIsCompleting] = useState(false);
  const chatEndRef = useRef<HTMLDivElement>(null);
  
  const scrollToBottom = () => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };
  
  // Store the current requirement ID
  const [requirementId, setRequirementId] = useState<number | null>(null);
  
  useEffect(() => {
    // Initialize with the first AI message once we have the feature text
    if (featureText && messages.length === 0) {
      setIsInitializing(true);
      toast.info("AI is analyzing your feature description...");
      
      // Create a requirement and analyze it
      const analyzeFeature = async () => {
        try {
          logger.info("Starting feature analysis process", { featureTextLength: featureText.length });
          
          // Create a requirement from the text
          const title = "Feature Request " + new Date().toLocaleString();
          logger.debug("Creating requirement with title", { title });
          
          try {
            const requirement = await requirementsApi.createRequirementFromText(title, featureText);
            logger.info("Requirement created successfully", { 
              requirementId: requirement.id,
              requirementTitle: requirement.title
            });
            
            setRequirementId(requirement.id);
            
            // Notify parent component about the requirement ID
            if (onRequirementCreated) {
              onRequirementCreated(requirement.id);
              logger.debug("Parent component notified of requirement ID", { id: requirement.id });
            }
            
            // Analyze the requirement
            logger.debug("Analyzing requirement", { id: requirement.id });
            const analysis = await requirementsApi.analyzeRequirement(requirement.id);
            logger.info("Requirement analysis completed", { 
              requirementId: requirement.id,
              analysisId: analysis.id
            });
          } catch (reqError: any) {
            logger.error("Error in requirement creation/analysis", {
              error: reqError.message,
              stack: reqError.stack,
              response: reqError.response ? {
                status: reqError.response.status,
                data: reqError.response.data
              } : 'No response data'
            });
            throw reqError; // Re-throw to be caught by outer catch
          }
          
          // Format the initial questions
          const initialQuestions = [
            "I've analyzed your feature request. Can you clarify the target users for this feature?",
            "What specific problem is this feature trying to solve?",
            "Are there any performance expectations or constraints for this feature?"
          ].join("\n\n");
          
          logger.debug("Setting initial AI messages");
          setMessages([
            {
              id: "initial-message",
              sender: "ai",
              content: initialQuestions,
              timestamp: new Date()
            }
          ]);
          
          setIsInitializing(false);
          toast.success("Analysis complete! Please answer the questions to continue.");
        } catch (error: any) {
          logger.error("Error analyzing feature", {
            error: error.message,
            stack: error.stack,
            response: error.response ? {
              status: error.response.status,
              data: error.response.data
            } : 'No response data'
          });
          console.error("Error analyzing feature:", error);
          
          // More descriptive error message
          if (error.response) {
            const status = error.response.status;
            if (status === 404) {
              toast.error("API endpoint not found. Please check server configuration.");
            } else if (status === 500) {
              toast.error("Server error during analysis. Using simulated response instead.");
            } else {
              toast.error(`Analysis failed with status ${status}. Using simulated response instead.`);
            }
          } else if (error.request) {
            toast.error("No response from server. Using simulated response instead.");
          } else {
            toast.error("Failed to analyze feature: " + error.message + ". Using simulated response instead.");
          }
          
          // Fallback to simulated response
          const initialQuestions = [
            "I've analyzed your feature request. Can you clarify the target users for this feature?",
            "What specific problem is this feature trying to solve?",
            "Are there any performance expectations or constraints for this feature?"
          ].join("\n\n");
          
          setMessages([
            {
              id: "initial-message",
              sender: "ai",
              content: initialQuestions,
              timestamp: new Date()
            }
          ]);
          
          setIsInitializing(false);
        }
      };
      
      analyzeFeature();
    }
  }, [featureText, messages.length]);
  
  useEffect(() => {
    scrollToBottom();
  }, [messages]);
  
  const handleSendMessage = useCallback(async () => {
    if (!input.trim() || isSending || !requirementId) return;
    
    logger.info("Processing user message", { inputLength: input.length });
    
    // Add user message
    const userMessage: Message = {
      id: `user-${Date.now()}`,
      sender: "user",
      content: input,
      timestamp: new Date()
    };
    
    setMessages(prev => [...prev, userMessage]);
    setInput("");
    setIsSending(true);
    setIsLoading(true);
    
    toast.info("Processing your response...");
    
    try {
      // Check if we've had enough back-and-forth (3-4 exchanges)
      if (messages.length >= 6) {
        logger.info("Conversation complete, generating implementation plan", { requirementId });
        
        try {
          // Generate implementation plan
          const plan = await requirementsApi.generateImplementationPlan(requirementId);
          logger.info("Implementation plan generated", { 
            requirementId,
            planId: plan.id
          });
        } catch (planError: any) {
          logger.error("Error generating implementation plan", {
            error: planError.message,
            requirementId,
            stack: planError.stack
          });
          // Continue with the flow even if plan generation fails
        }
        
        const completeMessage: Message = {
          id: `ai-${Date.now()}`,
          sender: "ai",
          content: "Thank you for the clarification. I now have a good understanding of your feature request. Let's proceed to the complexity analysis.",
          timestamp: new Date()
        };
        
        setMessages(prev => [...prev, completeMessage]);
        setIsLoading(false);
        setIsSending(false);
        
        toast.success("Feature clarification complete!");
        logger.info("Feature clarification completed successfully");
        
        // Wait a bit and then proceed to analysis
        setIsCompleting(true);
        
        setTimeout(() => {
          toast.info("Moving to complexity analysis...");
          logger.info("Navigating to complexity analysis");
          onComplete();
        }, 2000);
        
        return;
      }
      
      // Generate a follow-up question based on conversation length
      let followUpQuestion = "";
      
      switch (messages.length) {
        case 1:
          followUpQuestion = "Thank you for that information. Could you elaborate on any specific technical constraints or dependencies for this feature?";
          break;
        case 3:
          followUpQuestion = "That's helpful. Are there any specific user flows or edge cases we should consider?";
          break;
        default:
          followUpQuestion = "Could you please provide more details about how this integrates with existing functionality?";
      }
      
      const aiResponse: Message = {
        id: `ai-${Date.now()}`,
        sender: "ai",
        content: followUpQuestion,
        timestamp: new Date()
      };
      
      setMessages(prev => [...prev, aiResponse]);
      setIsLoading(false);
      setIsSending(false);
      toast.success("Response received");
    } catch (error) {
      console.error("Error processing message:", error);
      toast.error("Failed to process message. Please try again.");
      setIsLoading(false);
      setIsSending(false);
    }
  }, [input, isSending, messages.length, onComplete, requirementId]);
  
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };
  
  return (
    <Card className="flex flex-col h-full relative">
      {isInitializing && (
        <LoadingScreen 
          variant="overlay" 
          message="Analyzing feature description..." 
          icon="circle"
        />
      )}
      
      {isCompleting && (
        <LoadingScreen 
          variant="overlay" 
          message="Preparing complexity analysis..." 
          icon="circle"
        />
      )}
      
      <div className="p-4 border-b flex items-center justify-between">
        <div className="flex items-center">
          <Brain className="h-5 w-5 text-accent mr-2" />
          <h2 className="text-lg font-medium">Feature Clarification</h2>
        </div>
      </div>
      
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        <div className="bg-muted/30 rounded-lg p-4 mb-6">
          <h3 className="font-medium mb-2">Feature Description</h3>
          <p className="text-sm whitespace-pre-wrap text-muted-foreground">
            {featureText}
          </p>
        </div>
        
        {messages.map((message) => (
          <div 
            key={message.id}
            className={cn(
              "flex items-start gap-3 animate-fade-in", 
              message.sender === "user" ? "flex-row-reverse" : ""
            )}
          >
            <Avatar className={cn(
              "h-8 w-8 rounded-full",
              message.sender === "user" 
                ? "bg-primary text-primary-foreground" 
                : "bg-accent text-accent-foreground"
            )}>
              {message.sender === "user" ? (
                <User className="h-4 w-4" />
              ) : (
                <Brain className="h-4 w-4" />
              )}
            </Avatar>
            
            <div className={cn(
              "rounded-lg px-4 py-3 max-w-[80%]",
              message.sender === "user"
                ? "bg-primary text-primary-foreground" 
                : "bg-card border"
            )}>
              <p className="text-sm whitespace-pre-wrap">{message.content}</p>
              <span className="text-xs opacity-70 block mt-1">
                {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </span>
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="flex items-start gap-3">
            <Avatar className="h-8 w-8 rounded-full bg-accent text-accent-foreground">
              <Brain className="h-4 w-4" />
            </Avatar>
            <div className="flex space-x-2 p-3 bg-card border rounded-lg">
              <div className="h-2 w-2 bg-muted-foreground/30 rounded-full animate-pulse" />
              <div className="h-2 w-2 bg-muted-foreground/30 rounded-full animate-pulse" style={{ animationDelay: "0.2s" }} />
              <div className="h-2 w-2 bg-muted-foreground/30 rounded-full animate-pulse" style={{ animationDelay: "0.4s" }} />
            </div>
          </div>
        )}
        
        <div ref={chatEndRef} />
      </div>
      
      <div className="p-4 border-t">
        <div className="flex items-center space-x-2">
          <Textarea 
            placeholder="Type your response..." 
            className="min-h-[60px] resize-none"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            disabled={isLoading || isInitializing || isSending || isCompleting}
          />
          <Button 
            size="icon" 
            onClick={handleSendMessage} 
            disabled={!input.trim() || isLoading || isInitializing || isSending || isCompleting}
          >
            {isSending ? (
              <LoadingScreen variant="mini" icon="refresh" />
            ) : (
              <Send className="h-4 w-4" />
            )}
            <span className="sr-only">Send message</span>
          </Button>
        </div>
      </div>
    </Card>
  );
}
