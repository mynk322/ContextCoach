import { LogsViewer } from "@/components/logs-viewer";
import { FileText } from "lucide-react";

export default function Logs() {
  return (
    <div className="h-full flex flex-col">
      <div className="flex flex-col gap-6 p-6 bg-jira-lightBg">
        <div className="flex items-center space-x-2 mb-2">
          <FileText className="h-5 w-5 text-jira-blue" />
          <h1 className="text-xl font-medium text-jira-text">Application Logs</h1>
        </div>
        
        <div className="bg-white rounded-sm shadow-sm border border-jira-border">
          <div className="h-[calc(100vh-150px)]">
            <LogsViewer />
          </div>
        </div>
      </div>
    </div>
  );
}
