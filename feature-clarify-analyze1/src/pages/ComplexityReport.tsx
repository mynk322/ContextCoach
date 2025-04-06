
import { useState, useEffect } from "react";
import { ComplexityAnalysis } from "@/components/complexity-analysis";
import { useLocation } from "react-router-dom";
import { toast } from "sonner";

export default function ComplexityReport() {
  const [requirementId, setRequirementId] = useState<number | null>(null);
  const location = useLocation();
  
  useEffect(() => {
    // Check if we have a requirement ID in the location state
    if (location.state && location.state.requirementId) {
      setRequirementId(location.state.requirementId);
    } else {
      // If no requirement ID is provided, show a message
      toast.info("No specific requirement selected. Showing sample analysis.");
    }
  }, [location]);
  
  return (
    <div className="h-full flex flex-col">
      <div className="flex-grow">
        <ComplexityAnalysis requirementId={requirementId} />
      </div>
    </div>
  );
}
