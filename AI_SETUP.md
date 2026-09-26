# Optional real AI setup

DefectIQ works without an external model, but for the hackathon you can enable a local LLM with Ollama.

1. Install Ollama.
2. Pull a model:

```bash
ollama pull llama3.2
```

3. Start the backend with:

Linux/macOS:
```bash
OLLAMA_ENABLED=true OLLAMA_MODEL=llama3.2 mvn spring-boot:run
```

Windows PowerShell:
```powershell
$env:OLLAMA_ENABLED="true"
$env:OLLAMA_MODEL="llama3.2"
mvn spring-boot:run
```

The backend asks the local model to return structured JSON and then applies DefectIQ's deterministic quality scoring and validation.

If the model is unavailable or produces invalid JSON, the system automatically falls back to the built-in enhancer, so the demo does not fail.
