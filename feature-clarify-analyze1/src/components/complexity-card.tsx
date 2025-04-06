
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ChevronDown, ChevronUp } from "lucide-react";
import { cn } from "@/lib/utils";

interface ComplexityCardProps {
  title: string;
  icon: React.ElementType;
  children: React.ReactNode;
  className?: string;
  defaultExpanded?: boolean;
}

export function ComplexityCard({ title, icon: Icon, children, className, defaultExpanded = false }: ComplexityCardProps) {
  const [isExpanded, setIsExpanded] = useState(defaultExpanded);
  
  return (
    <Card className={cn("transition-all", className)}>
      <CardHeader className="p-4 flex flex-row items-center justify-between space-y-0">
        <div className="flex items-center space-x-3">
          <div className="bg-primary/10 p-2 rounded-md">
            <Icon className="h-5 w-5 text-primary" />
          </div>
          <CardTitle className="text-base font-medium">{title}</CardTitle>
        </div>
        <Button
          variant="ghost"
          size="sm"
          className="h-8 w-8 p-0"
          onClick={() => setIsExpanded(!isExpanded)}
        >
          {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
          <span className="sr-only">{isExpanded ? "Collapse" : "Expand"}</span>
        </Button>
      </CardHeader>
      <div className={cn(
        "grid transition-all duration-200 ease-in-out",
        isExpanded ? "grid-rows-[1fr] opacity-100" : "grid-rows-[0fr] opacity-0"
      )}>
        <div className="overflow-hidden">
          <CardContent className="p-4 pt-0">
            {children}
          </CardContent>
        </div>
      </div>
    </Card>
  );
}
