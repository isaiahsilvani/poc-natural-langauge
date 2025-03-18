This is a POC to demonstrate how we can map Natural Language prompts to pre-defined actions.

Essentially, we force the AI to return an expected structured JSON response, and parse the JSON to a inner class in a sealed class hieracy, which can represent the action to take. 

You can check the the **sendPrompt** method in **NailViewModel** to see the related code
