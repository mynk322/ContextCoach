
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import FeatureClarification from "./pages/FeatureClarification";
import ComplexityReport from "./pages/ComplexityReport";
import Logs from "./pages/Logs";
import NotFound from "./pages/NotFound";
import { useLocation } from "react-router-dom";
import { useIsMobile } from "./hooks/use-mobile";
import { useState, useEffect } from "react";
import { LoadingScreen } from "./components/loading-screen";

const queryClient = new QueryClient();

const AppContent = () => {
  const location = useLocation();
  const isMobile = useIsMobile();
  const [currentPhase, setCurrentPhase] = useState("clarification");
  const [isLoading, setIsLoading] = useState(true);
  
  useEffect(() => {
    if (location.pathname === "/") {
      setCurrentPhase("clarification");
    } else if (location.pathname === "/analysis") {
      setCurrentPhase("analysis");
    }
    
    // Simulate initial loading
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 2000);
    
    return () => clearTimeout(timer);
  }, [location.pathname]);
  
  // Show loading screen while app is initializing
  if (isLoading) {
    return (
      <div className="h-screen w-full flex items-center justify-center bg-background">
        <LoadingScreen message="Initializing ContextCoach..." />
      </div>
    );
  }
  
  return (
    <SidebarProvider defaultOpen={!isMobile}>
      <div className="h-screen flex w-full">
        <AppSidebar currentPhase={currentPhase} />
        <div className="flex-1 flex flex-col overflow-hidden">
          <div className="h-14 border-b flex items-center px-4">
            <SidebarTrigger />
            <div className="ml-4">
              <h1 className="text-lg font-medium">Feature Complexity Analyzer</h1>
            </div>
          </div>
          <div className="flex-1 overflow-auto p-4">
            <Routes>
              <Route path="/" element={<FeatureClarification />} />
              <Route path="/analysis" element={<ComplexityReport />} />
              <Route path="/logs" element={<Logs />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </div>
        </div>
      </div>
    </SidebarProvider>
  );
};

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <AppContent />
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
