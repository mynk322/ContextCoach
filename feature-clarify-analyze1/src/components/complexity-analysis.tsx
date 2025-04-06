
import { useState, useEffect } from "react";
import { requirementsApi } from "@/services/api";
import { LoadingScreen } from "@/components/loading-screen";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ComplexityCard } from "./complexity-card";
import { 
  Activity, 
  AlertCircle, 
  BarChart4, 
  CheckCircle2, 
  Code2, 
  CodeSquare, 
  Download, 
  FileCode2, 
  ListTodo, 
  RefreshCw, 
  Shield 
} from "lucide-react";
import { toast } from "sonner";

// Default analysis data structure
interface AnalysisData {
  complexityLevel: string;
  storyPoints: number;
  affectedFiles: Array<{
    name: string;
    path: string;
    changes: string;
  }>;
  subtasks: Array<{
    id: number;
    title: string;
    estimate: number;
    priority: string;
  }>;
  refactorRecommendations: string[];
  edgeCases: string[];
}

// Default mock data as fallback
const defaultAnalysisData: AnalysisData = {
  complexityLevel: "Medium",
  storyPoints: 8,
  affectedFiles: [
    { name: "UserRepository.java", path: "src/main/java/com/example/repository/UserRepository.java", changes: "moderate" },
    { name: "UserService.java", path: "src/main/java/com/example/service/UserService.java", changes: "significant" },
    { name: "AuthController.java", path: "src/main/java/com/example/controller/AuthController.java", changes: "minor" },
    { name: "UserDTO.java", path: "src/main/java/com/example/dto/UserDTO.java", changes: "minor" }
  ],
  subtasks: [
    { id: 1, title: "Update user repository with new query methods", estimate: 2, priority: "high" },
    { id: 2, title: "Implement new service methods for feature", estimate: 3, priority: "high" },
    { id: 3, title: "Add validation for new input parameters", estimate: 1, priority: "medium" },
    { id: 4, title: "Update API controller endpoints", estimate: 2, priority: "medium" },
  ],
  refactorRecommendations: [
    "Extract duplicate validation logic in AuthController to a separate utility class",
    "Consider using a builder pattern for the complex UserDTO construction",
    "Implement pagination for the user search results to improve performance"
  ],
  edgeCases: [
    "Handle case when user has no permissions to access requested data",
    "Consider rate limiting for API endpoints to prevent abuse",
    "Ensure proper error handling for network timeouts during external service calls",
    "Add validation for edge cases in search parameters"
  ]
};

interface ComplexityAnalysisProps {
  requirementId?: number;
}

export function ComplexityAnalysis({ requirementId }: ComplexityAnalysisProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [analysisData, setAnalysisData] = useState<AnalysisData>(defaultAnalysisData);
  const [isLoadingAnalysis, setIsLoadingAnalysis] = useState(false);
  
  useEffect(() => {
    // If we have a requirement ID, fetch the analysis data
    if (requirementId) {
      setIsLoadingAnalysis(true);
      toast.info("Loading complexity analysis...");
      
      // Fetch the analysis data
      const fetchAnalysisData = async () => {
        try {
          // Get the scope estimation
          const scopeEstimation = await requirementsApi.estimateScope(requirementId);
          
          // Get the implementation plan
          const implementationPlan = await requirementsApi.generateImplementationPlan(requirementId);
          
          // Get the story points
          const storyPoints = await requirementsApi.calculateStoryPoints(requirementId);
          
          // Combine the data
          const combinedData: AnalysisData = {
            complexityLevel: scopeEstimation.complexityLevel || "Medium",
            storyPoints: storyPoints.points || 8,
            affectedFiles: implementationPlan.affectedFiles || defaultAnalysisData.affectedFiles,
            subtasks: implementationPlan.tasks || defaultAnalysisData.subtasks,
            refactorRecommendations: implementationPlan.refactorRecommendations || defaultAnalysisData.refactorRecommendations,
            edgeCases: scopeEstimation.edgeCases || defaultAnalysisData.edgeCases
          };
          
          setAnalysisData(combinedData);
          toast.success("Analysis loaded successfully!");
        } catch (error) {
          console.error("Error loading analysis data:", error);
          toast.error("Failed to load analysis data. Using default data instead.");
          // Keep using the default data
        } finally {
          setIsLoadingAnalysis(false);
        }
      };
      
      fetchAnalysisData();
    }
  }, [requirementId]);
  
  const getComplexityColor = (level: string) => {
    switch (level.toLowerCase()) {
      case "low": return "bg-green-500/10 text-green-600 dark:text-green-400";
      case "medium": return "bg-yellow-500/10 text-yellow-600 dark:text-yellow-400";
      case "high": return "bg-red-500/10 text-red-600 dark:text-red-400";
      default: return "bg-blue-500/10 text-blue-600 dark:text-blue-400";
    }
  };
  
  const getChangeColor = (change: string) => {
    switch (change.toLowerCase()) {
      case "minor": return "bg-green-500/10 text-green-600 dark:text-green-400 border-green-200 dark:border-green-900/30";
      case "moderate": return "bg-yellow-500/10 text-yellow-600 dark:text-yellow-400 border-yellow-200 dark:border-yellow-900/30";
      case "significant": return "bg-red-500/10 text-red-600 dark:text-red-400 border-red-200 dark:border-red-900/30";
      default: return "bg-blue-500/10 text-blue-600 dark:text-blue-400 border-blue-200 dark:border-blue-900/30";
    }
  };
  
  const getPriorityColor = (priority: string) => {
    switch (priority.toLowerCase()) {
      case "low": return "bg-green-500/10 text-green-600 dark:text-green-400";
      case "medium": return "bg-yellow-500/10 text-yellow-600 dark:text-yellow-400";
      case "high": return "bg-red-500/10 text-red-600 dark:text-red-400";
      default: return "bg-blue-500/10 text-blue-600 dark:text-blue-400";
    }
  };
  
  const downloadJson = async () => {
    setIsLoading(true);
    
    try {
      const dataStr = JSON.stringify(analysisData, null, 2);
      const dataUri = `data:application/json;charset=utf-8,${encodeURIComponent(dataStr)}`;
      
      const exportFileDefaultName = `feature-analysis-${new Date().toISOString().slice(0, 10)}.json`;
      
      const linkElement = document.createElement('a');
      linkElement.setAttribute('href', dataUri);
      linkElement.setAttribute('download', exportFileDefaultName);
      linkElement.click();
      
      toast.success("Analysis JSON downloaded successfully");
    } catch (error) {
      toast.error("Failed to download analysis JSON");
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <Card className="flex flex-col h-full">
      <div className="p-4 border-b flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center">
          <BarChart4 className="h-5 w-5 text-accent mr-2" />
          <h2 className="text-lg font-medium">Complexity Analysis</h2>
        </div>
        
        <div className="flex items-center space-x-2">
          <Button
            size="sm"
            variant="outline"
            onClick={downloadJson}
            disabled={isLoading}
            className="gap-2"
          >
            {isLoading ? (
              <RefreshCw className="h-4 w-4 animate-spin" />
            ) : (
              <Download className="h-4 w-4" />
            )}
            <span>Download JSON</span>
          </Button>
        </div>
      </div>
      
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        <div className="grid gap-4 md:grid-cols-2">
          {isLoadingAnalysis ? (
            <div className="md:col-span-2 flex justify-center p-8">
              <LoadingScreen variant="inline" message="Loading analysis data..." icon="circle" />
            </div>
          ) : (
            <>
              <ComplexityCard
                title="Complexity Level" 
                icon={Activity}
                defaultExpanded
                className="md:col-span-1"
              >
                <div className="flex items-center space-x-2">
                  <Badge className={getComplexityColor(analysisData.complexityLevel)} variant="outline">
                    {analysisData.complexityLevel}
                  </Badge>
                  <span className="text-sm text-muted-foreground">
                    This feature has a {analysisData.complexityLevel.toLowerCase()} implementation complexity
                  </span>
                </div>
              </ComplexityCard>
              
              <ComplexityCard
                title="Estimated Story Points" 
                icon={BarChart4}
                defaultExpanded
                className="md:col-span-1"
              >
                <div className="flex flex-col">
                  <div className="text-3xl font-bold">{analysisData.storyPoints}</div>
                  <p className="text-sm text-muted-foreground">
                    Based on complexity and scope analysis
                  </p>
                </div>
              </ComplexityCard>
            </>
          )}
        </div>
        
        {isLoadingAnalysis ? (
          <div className="flex justify-center p-8">
            <LoadingScreen variant="inline" message="Loading affected files..." icon="circle" />
          </div>
        ) : (
          <ComplexityCard
            title="Affected Files/Modules" 
            icon={FileCode2}
            defaultExpanded
          >
            <div className="space-y-3">
              {analysisData.affectedFiles.map((file, index) => (
              <div
                key={index}
                className="flex flex-col md:flex-row md:items-center justify-between p-3 rounded-lg border bg-card/50"
              >
                <div className="flex items-center gap-3">
                  <CodeSquare className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <p className="font-medium text-sm">{file.name}</p>
                    <p className="text-xs text-muted-foreground">{file.path}</p>
                  </div>
                </div>
                <Badge 
                  className={`${getChangeColor(file.changes)} mt-2 md:mt-0`} 
                  variant="outline"
                >
                  {file.changes} changes
                </Badge>
              </div>
              ))}
            </div>
          </ComplexityCard>
        )}
        
        {isLoadingAnalysis ? (
          <div className="flex justify-center p-8">
            <LoadingScreen variant="inline" message="Loading subtasks..." icon="circle" />
          </div>
        ) : (
          <ComplexityCard
            title="Suggested Subtasks"
            icon={ListTodo}
            defaultExpanded
          >
            <div className="space-y-3">
              {analysisData.subtasks.map((task) => (
              <div
                key={task.id}
                className="flex flex-col md:flex-row md:items-center justify-between p-3 rounded-lg border bg-card/50"
              >
                <div className="flex items-start gap-3">
                  <div className="pt-0.5">
                    <CheckCircle2 className="h-4 w-4 text-muted-foreground" />
                  </div>
                  <p className="text-sm">{task.title}</p>
                </div>
                <div className="flex items-center gap-2 mt-2 md:mt-0">
                  <Badge variant="outline" className="bg-primary/10 text-primary">
                    {task.estimate} {task.estimate === 1 ? 'point' : 'points'}
                  </Badge>
                  <Badge className={getPriorityColor(task.priority)} variant="outline">
                    {task.priority}
                  </Badge>
                </div>
              </div>
              ))}
            </div>
          </ComplexityCard>
        )}
        
        {isLoadingAnalysis ? (
          <div className="flex justify-center p-8">
            <LoadingScreen variant="inline" message="Loading recommendations..." icon="circle" />
          </div>
        ) : (
          <ComplexityCard
            title="Refactor Recommendations"
            icon={Code2}
          >
            <ul className="list-disc list-inside space-y-2">
              {analysisData.refactorRecommendations.map((recommendation, index) => (
                <li key={index} className="text-sm">
                  {recommendation}
                </li>
              ))}
            </ul>
          </ComplexityCard>
        )}
        
        {isLoadingAnalysis ? (
          <div className="flex justify-center p-8">
            <LoadingScreen variant="inline" message="Loading edge cases..." icon="circle" />
          </div>
        ) : (
          <ComplexityCard
            title="Edge Cases & Risks"
            icon={AlertCircle}
          >
            <ul className="list-disc list-inside space-y-2">
              {analysisData.edgeCases.map((edge, index) => (
                <li key={index} className="text-sm">
                  {edge}
                </li>
              ))}
            </ul>
          </ComplexityCard>
        )}
      </div>
    </Card>
  );
}
