
import React from "react";
import { Loader, LoaderCircle, RefreshCw } from "lucide-react";

interface LoadingScreenProps {
  message?: string;
  variant?: "default" | "inline" | "overlay" | "mini";
  icon?: "default" | "circle" | "refresh";
  className?: string;
}

export function LoadingScreen({ 
  message = "Loading...", 
  variant = "default",
  icon = "default",
  className = ""
}: LoadingScreenProps) {
  let LoaderIcon;
  
  switch (icon) {
    case "circle":
      LoaderIcon = LoaderCircle;
      break;
    case "refresh":
      LoaderIcon = RefreshCw;
      break;
    default:
      LoaderIcon = Loader;
  }
  
  if (variant === "mini") {
    return (
      <div className={`inline-flex items-center ${className}`}>
        <LoaderIcon className="h-3 w-3 text-jira-blue animate-spin" />
      </div>
    );
  }
  
  if (variant === "inline") {
    return (
      <div className={`flex items-center gap-2 py-2 ${className}`}>
        <LoaderIcon className="h-4 w-4 text-jira-blue animate-spin" />
        <p className="text-sm text-jira-muted">{message}</p>
      </div>
    );
  }
  
  if (variant === "overlay") {
    return (
      <div className={`absolute inset-0 bg-white/90 backdrop-blur-sm flex flex-col items-center justify-center z-50 ${className}`}>
        <LoaderIcon className="h-12 w-12 text-jira-blue animate-spin mb-4" />
        <p className="text-lg text-jira-text animate-pulse">{message}</p>
      </div>
    );
  }
  
  return (
    <div className={`flex flex-col items-center justify-center h-full w-full ${className}`}>
      <LoaderIcon className="h-12 w-12 text-jira-blue animate-spin mb-4" />
      <p className="text-lg text-jira-text">{message}</p>
    </div>
  );
}
