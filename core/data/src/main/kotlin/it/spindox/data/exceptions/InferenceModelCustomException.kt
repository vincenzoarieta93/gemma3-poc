package it.spindox.data.exceptions

import java.lang.RuntimeException


abstract class LlmInferenceException(message: String? = null): RuntimeException(message)
class InferenceModelNotFoundException(message: String): LlmInferenceException(message)
class InferenceEngineNotInitializedException(): LlmInferenceException()
class CreateLlmInferenceTaskException(): LlmInferenceException()
class CreateSessionInferenceTaskException(): LlmInferenceException()
class ResetSessionInferenceTaskException(): LlmInferenceException()