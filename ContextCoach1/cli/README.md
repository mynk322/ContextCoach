# Feature Complexity Analysis CLI

A command-line tool for analyzing the complexity of feature requests in software development projects.

## Overview

The Feature Complexity Analysis CLI tool is organized into two phases:

1. **Feature Clarification** - The tool uses a FeatureClarifier to interactively resolve ambiguities in the user-provided feature description. It searches a vector database for relevant code context and uses an LLM (Large Language Model) to identify unclear terms or gaps. The tool then prompts the user with clarifying questions and updates the feature description until it's sufficiently clear.

2. **Complexity Analysis** - Once the feature description is clarified, a ComplexityAnalyzer retrieves related code snippets (via vector DB search) and invokes the LLM to analyze implementation complexity. The LLM assesses complexity (low/medium/high), estimates story points, identifies affected components, lists subtasks, suggests refactors, and highlights risks. The final result is printed as a structured JSON report.

## Usage

### Running the CLI Tool

```bash
# From the project root directory
./cli/feature-complexity-cli.sh

# Or with a feature request file
./cli/feature-complexity-cli.sh cli/examples/sample-feature-request.txt
```

### Example Feature Request

See `examples/sample-feature-request.txt` for an example feature request.

## Architecture

The CLI tool is built with the following components:

- **LLMService** - Interface for LLM interactions, with implementations:
  - **RabbitHoleLLMService** - Uses RabbitHole API via the existing RabbitHoleService
  - **DummyLLMService** - Provides simulated responses for testing

- **VectorDB** - Interface for vector database search operations, with implementation:
  - **DummyVectorDB** - Provides simulated code snippets for testing

- **FeatureClarifier** - Handles Phase 1, querying the vector DB and using LLM Q&A to clarify the feature description with the user.

- **ComplexityAnalyzer** - Handles Phase 2, performing vector DB searches and using the LLM to generate a complexity analysis.

- **FeatureComplexityCLI** - The main class with the main method, orchestrating the CLI flow.

## Integration with ContextCoach

The Feature Complexity Analysis CLI is integrated with the main ContextCoach application, leveraging its existing services like RabbitHoleService for LLM integration.
