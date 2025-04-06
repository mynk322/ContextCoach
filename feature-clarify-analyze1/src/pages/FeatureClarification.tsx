
import { useState } from "react";
import { FeatureUpload } from "@/components/feature-upload";
import { ChatInterface } from "@/components/chat-interface";
import { useNavigate } from "react-router-dom";
import { LoadingScreen } from "@/components/loading-screen";
import { toast } from "sonner";
import { Users, BrainCircuit, LineChart } from "lucide-react";

export default function FeatureClarification() {
  const [featureText, setFeatureText] = useState<string>("");
  const [stage, setStage] = useState<"upload" | "clarification" | "loading" | "navigating">("upload");
  const navigate = useNavigate();
  
  const handleFeatureSubmit = (text: string) => {
    setFeatureText(text);
    setStage("loading");
    
    // Simulate processing time
    toast.info("Initializing feature clarification...");
    setTimeout(() => {
      setStage("clarification");
    }, 1500);
  };
  
  // Store the current requirement ID
  const [requirementId, setRequirementId] = useState<number | null>(null);
  
  const handleClarificationComplete = () => {
    setStage("navigating");
    
    // Simulate transition delay
    toast.info("Preparing complexity analysis...");
    setTimeout(() => {
      navigate("/analysis", { state: { requirementId } });
    }, 1500);
  };
  
  // Callback to set the requirement ID from the chat interface
  const handleRequirementCreated = (id: number) => {
    setRequirementId(id);
  };
  
  return (
    <div className="h-full flex flex-col">
      {stage === "upload" ? (
        <div className="flex flex-col gap-6 p-6 bg-jira-lightBg">
          <div className="flex items-center space-x-2 mb-2">
            <Users className="h-5 w-5 text-jira-blue" />
            <h1 className="text-xl font-medium text-jira-text">Feature Assessment</h1>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2">
              <div className="bg-white rounded-sm shadow-sm border border-jira-border p-6">
                <h2 className="text-lg font-medium mb-4 text-jira-text">Upload Feature Description</h2>
                <FeatureUpload onFeatureSubmit={handleFeatureSubmit} />
              </div>
            </div>
            
            <div className="space-y-6">
              <div className="bg-white rounded-sm shadow-sm border border-jira-border p-4">
                <div className="flex items-center space-x-2 mb-3">
                  <BrainCircuit className="h-4 w-4 text-jira-blue" />
                  <h3 className="font-medium text-jira-text">Process Overview</h3>
                </div>
                <ol className="space-y-2 text-sm text-jira-muted">
                  <li className="flex items-start gap-2">
                    <span className="bg-jira-blue text-white rounded-full w-5 h-5 flex items-center justify-center text-xs flex-shrink-0">1</span>
                    <span>Upload your feature description</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <span className="bg-jira-muted text-white rounded-full w-5 h-5 flex items-center justify-center text-xs flex-shrink-0">2</span>
                    <span>Clarify requirements through conversation</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <span className="bg-jira-muted text-white rounded-full w-5 h-5 flex items-center justify-center text-xs flex-shrink-0">3</span>
                    <span>Review complexity analysis report</span>
                  </li>
                </ol>
              </div>
              
              <div className="bg-white rounded-sm shadow-sm border border-jira-border p-4">
                <div className="flex items-center space-x-2 mb-3">
                  <LineChart className="h-4 w-4 text-jira-blue" />
                  <h3 className="font-medium text-jira-text">Report Benefits</h3>
                </div>
                <ul className="space-y-2 text-sm text-jira-muted">
                  <li>• Accurate complexity scoring</li>
                  <li>• Effort estimation guidance</li>
                  <li>• Implementation risk assessment</li>
                  <li>• Developer resource planning</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      ) : stage === "loading" ? (
        <LoadingScreen message="Initializing feature clarification..." icon="circle" />
      ) : stage === "navigating" ? (
        <LoadingScreen message="Preparing complexity analysis..." icon="refresh" />
      ) : (
        <div className="flex-grow bg-jira-lightBg">
          <div className="max-w-6xl mx-auto p-4">
            <div className="bg-white rounded-sm shadow-sm border border-jira-border">
              <ChatInterface 
                featureText={featureText} 
                onComplete={handleClarificationComplete}
                onRequirementCreated={handleRequirementCreated}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
