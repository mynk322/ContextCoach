{
  "ambiguityCategories": [
    "Missing Context",
    "Incomplete Requirements",
    "Unclear Terminology",
    "Undefined Metrics",
    "Missing Technical Specifications"
  ],
  "analysis": "The requirement contains several significant ambiguities: 1) It refers to a 'backfill flow re-architecture' without defining what this is or providing necessary context. 2) 'Deepgram' is mentioned without explaining what it is (presumably an audio processing API). 3) The relationship between the backfill flow architecture and Deepgram is not explained. 4) While it specifies '2M calls' and '5mins' average audio size, it doesn't specify the audio format, quality, or sampling rate which would affect processing costs. 5) 'Lower environment' is undefined - it could mean development, testing, or staging environments. 6) The timeline for the testing is not specified. 7) The requirement doesn't clarify whether the 2M calls should be simultaneous, sequential, or distributed over time. 8) There's no mention of required performance metrics to be measured or success criteria.",
  "confidenceScore": 0.2,
  "suggestedImprovements": "1) Define what 'backfill flow re-architecture' means in this context and explain its purpose. 2) Specify what Deepgram is and which specific Deepgram API/service is being used. 3) Clarify which 'lower environment' is intended for testing (dev, test, staging). 4) Specify technical details of audio files: format (MP3, WAV, etc.), bitrate, channels, and sampling rate. 5) Define the call pattern requirements - concurrent calls, distribution over time, peak load requirements. 6) Specify the timeline for conducting the load tests. 7) Include success criteria and metrics to be measured during testing (response time, throughput, error rates). 8) Clarify what cost elements need to be estimated (just Deepgram API costs or total testing costs including infrastructure). 9) Specify any additional requirements regarding test data preparation or reporting needs."
}


You are an expert software architect and codebase analyst. Your task is to help assess the impact and effort of adding a new feature to an existing codebase.

**Repository Context:** {REPOSITORY_CONTEXT}  
**New Feature Description:** {FEATURE_DESCRIPTION}

**Steps to follow:**

1. **Information Gathering Queries:** Propose a step-by-step plan to retrieve relevant information about the current design and implementation from the repository (via vector database search). Focus on components, modules, or functions likely to be involved in or affected by the new feature. For each step, specify what to search for (e.g. specific keywords, module names, or architecture documents) to understand how the codebase works in those areas.

2. **Complexity Analysis:** Based on the repository context and the feature requirements, analyze the complexity of integrating the new functionality into the codebase. Consider the existing design, dependencies, and any necessary changes.

**Output Format:** Provide the analysis with the following sections, using clear markdown headings and lists for readability:

- **Step-by-Step Information Gathering:** A numbered list of specific queries or steps to find relevant code sections or documentation (for example, searching for related classes, functions, or design docs that pertain to the new feature).
- **Complexity Level:** An overall assessment of the implementation difficulty (e.g. *Low*, *Medium*, or *High* complexity) with a brief explanation of why this level was chosen.
- **Estimated Story Points:** An estimation of the effort required in Agile story points to implement the new feature.
- **Suggested Subtasks:** Bullet points for discrete subtasks or user stories needed to implement the feature (e.g. update database schema, modify backend logic, adjust UI components, write tests, etc.).
- **Affected Files/Modules:** Bullet points listing key files, classes, or modules that would be created, modified, or need attention when adding the feature.
- **Refactor Suggestions:** Bullet points for any refactoring or improvements to existing code that would make integration easier or improve code quality as part of this work.
- **Potential Edge Cases/Risks:** Bullet points listing any edge cases, potential bugs, or risks that should be considered during implementation and testing.

Make sure each section is thorough yet concise. Use an informative and **clear tone**, and format the response for easy reading (with lists and brief explanations as needed).