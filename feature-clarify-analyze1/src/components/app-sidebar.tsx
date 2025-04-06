
import {
  Brain,
  FileCode,
  LayoutDashboard,
  MessageSquare,
  Settings,
  FileText
} from "lucide-react";
import { useLocation, Link } from "react-router-dom";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { ThemeToggle } from "./theme-toggle";
import { cn } from "@/lib/utils";

// Menu items
const items = [
  {
    title: "Feature Clarification",
    path: "/",
    icon: MessageSquare,
    section: "clarification"
  },
  {
    title: "Complexity Analysis",
    path: "/analysis",
    icon: LayoutDashboard,
    section: "analysis"
  },
  {
    title: "Application Logs",
    path: "/logs",
    icon: FileText,
    section: "logs"
  },
];

export function AppSidebar({ currentPhase }: { currentPhase: string }) {
  const location = useLocation();

  return (
    <Sidebar className="border-r">
      <SidebarContent className="flex flex-col h-full">
        <div className="flex items-center px-4 py-6">
          <Brain className="h-6 w-6 text-accent mr-2" />
          <h1 className="text-xl font-bold">ContextCoach</h1>
        </div>
        
        <SidebarGroup>
          <SidebarGroupLabel>Analyze</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.title} className={cn({
                  'bg-sidebar-accent': currentPhase === item.section
                })}>
                  <SidebarMenuButton asChild className={cn("flex items-center")}>
                    <Link to={item.path} className={cn("flex items-center w-full")}>
                      <item.icon className="h-5 w-5 mr-3" />
                      <span>{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
        
        <div className="mt-auto p-4 flex items-center justify-between border-t">
          <div className="flex items-center">
            <FileCode className="h-5 w-5 text-muted-foreground" />
            <span className="text-sm text-muted-foreground ml-2">v1.0.0</span>
          </div>
          <ThemeToggle />
        </div>
      </SidebarContent>
    </Sidebar>
  );
}
