
import { useState, useCallback } from "react";
import { requirementsApi } from "@/services/api";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Card } from "@/components/ui/card";
import { Upload, FileText } from "lucide-react";
import { toast } from "sonner";
import { LoadingScreen } from "@/components/loading-screen";
import logger from "@/lib/logger";

interface FeatureUploadProps {
  onFeatureSubmit: (featureText: string) => void;
}

export function FeatureUpload({ onFeatureSubmit }: FeatureUploadProps) {
  const [featureText, setFeatureText] = useState("");
  const [isDragging, setIsDragging] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [isReadingFile, setIsReadingFile] = useState(false);
  
  const handleSubmit = async () => {
    if (!featureText.trim()) {
      toast.error("Please provide a feature description first");
      return;
    }
    setIsProcessing(true);
    
    toast.info("Processing feature description...");
    logger.info("Starting feature submission process", { textLength: featureText.length });
    
    try {
      // Create a requirement from the text
      const title = "Feature Request " + new Date().toLocaleString();
      logger.debug("Creating requirement with title", { title });
      
      // Log the request details
      logger.debug("Sending request to create requirement", { 
        title, 
        contentPreview: featureText.substring(0, 100) + (featureText.length > 100 ? '...' : '') 
      });
      
      const requirement = await requirementsApi.createRequirementFromText(title, featureText);
      
      // Log the response
      logger.info("Requirement created successfully", { 
        requirementId: requirement.id,
        requirementTitle: requirement.title
      });
      
      // Pass the requirement to the parent component
      onFeatureSubmit(featureText);
      toast.success("Feature submitted successfully!");
    } catch (error: any) {
      // Enhanced error logging
      logger.error("Error submitting feature", { 
        error: error.message,
        stack: error.stack,
        response: error.response ? {
          status: error.response.status,
          data: error.response.data
        } : 'No response data'
      });
      
      // More descriptive error message
      if (error.response) {
        const status = error.response.status;
        if (status === 400) {
          toast.error("Invalid feature data. Please check your input and try again.");
        } else if (status === 404) {
          toast.error("API endpoint not found. Please check server configuration.");
        } else if (status === 500) {
          toast.error("Server error. Please try again later or contact support.");
        } else {
          toast.error(`Request failed with status ${status}. Please try again.`);
        }
      } else if (error.request) {
        toast.error("No response from server. Please check your connection.");
      } else {
        toast.error("Failed to submit feature: " + error.message);
      }
      
      console.error("Error submitting feature:", error);
    } finally {
      setIsProcessing(false);
      logger.info("Feature submission process completed");
    }
  };
  
  const handleFileUpload = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    
    if (file.type !== "text/plain" && !file.name.endsWith(".txt") && !file.name.endsWith(".md")) {
      toast.error("Please upload a text file (.txt or .md)");
      return;
    }
    
    setIsReadingFile(true);
    toast.info("Reading file...");
    
    const reader = new FileReader();
    reader.onload = (event) => {
      const content = event.target?.result as string;
      setFeatureText(content);
      setIsReadingFile(false);
      toast.success("File content loaded successfully");
    };
    reader.onerror = () => {
      toast.error("Error reading the file");
      setIsReadingFile(false);
    };
    reader.readAsText(file);
    
    // Reset the input value so the same file can be selected again
    e.target.value = '';
  }, []);
  
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };
  
  const handleDragLeave = () => {
    setIsDragging(false);
  };
  
  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    
    const file = e.dataTransfer.files?.[0];
    if (!file) return;
    
    if (file.type !== "text/plain" && !file.name.endsWith(".txt") && !file.name.endsWith(".md")) {
      toast.error("Please upload a text file (.txt or .md)");
      return;
    }
    
    setIsReadingFile(true);
    toast.info("Reading file...");
    
    const reader = new FileReader();
    reader.onload = (event) => {
      const content = event.target?.result as string;
      setFeatureText(content);
      setIsReadingFile(false);
      toast.success("File content loaded successfully");
    };
    reader.onerror = () => {
      toast.error("Error reading the file");
      setIsReadingFile(false);
    };
    reader.readAsText(file);
  }, []);
  
  return (
    <Card className="p-6 shadow-lg relative">
      {isProcessing && (
        <LoadingScreen 
          variant="overlay" 
          message="Processing your feature..." 
          icon="circle"
        />
      )}
      
      <div className="space-y-6">
        <div className="space-y-2">
          <h2 className="text-2xl font-bold tracking-tight">Feature Description</h2>
          <p className="text-sm text-muted-foreground">
            Upload or paste a text file describing the feature you want to analyze.
          </p>
        </div>
        
        <div
          className={`border-2 border-dashed rounded-lg p-6 transition-all ${
            isDragging 
              ? "border-primary bg-primary/5" 
              : "border-muted-foreground/30 hover:border-muted-foreground/50"
          } ${isReadingFile ? 'bg-muted/20' : ''}`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          {isReadingFile ? (
            <div className="flex flex-col items-center justify-center space-y-4 text-center">
              <LoadingScreen variant="inline" message="Reading file..." icon="refresh" />
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center space-y-4 text-center">
              <div className="p-3 bg-muted rounded-full">
                <FileText className="h-8 w-8 text-muted-foreground" />
              </div>
              <div className="space-y-2">
                <p className="text-sm font-medium">
                  Drag and drop your feature description file
                </p>
                <p className="text-xs text-muted-foreground">
                  or click to browse files (TXT, MD)
                </p>
              </div>
              <label htmlFor="file-upload" className="w-auto">
                <input
                  id="file-upload"
                  type="file"
                  className="sr-only"
                  accept=".txt,.md,text/plain"
                  onChange={handleFileUpload}
                  disabled={isProcessing || isReadingFile}
                />
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="cursor-pointer" 
                  disabled={isProcessing || isReadingFile}
                  onClick={() => document.getElementById('file-upload')?.click()}
                  type="button"
                >
                  <Upload className="h-4 w-4 mr-2" />
                  Browse Files
                </Button>
              </label>
            </div>
          )}
        </div>
        
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <label htmlFor="feature-description" className="text-sm font-medium">
              Or paste your feature description
            </label>
          </div>
          <Textarea
            id="feature-description"
            className="min-h-[200px] font-mono text-sm"
            placeholder="Describe the feature you want to implement..."
            value={featureText}
            onChange={(e) => setFeatureText(e.target.value)}
            disabled={isProcessing || isReadingFile}
          />
        </div>
        
        <Button 
          className="w-full" 
          onClick={handleSubmit}
          disabled={!featureText.trim() || isProcessing || isReadingFile}
        >
          {isProcessing ? (
            <LoadingScreen variant="inline" message="Processing..." icon="refresh" />
          ) : (
            "Analyze Feature"
          )}
        </Button>
      </div>
    </Card>
  );
}
