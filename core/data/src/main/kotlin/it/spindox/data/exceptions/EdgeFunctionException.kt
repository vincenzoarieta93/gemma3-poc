package it.spindox.data.exceptions

import java.lang.RuntimeException


abstract class EdgeFunctionException(message: String? = null): RuntimeException(message)
class NoChatSessionException(): EdgeFunctionException("No chat session found")
