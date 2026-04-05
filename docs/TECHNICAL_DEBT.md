# Technical Debt — EwigChatV2

## 1. Message sending is not resilient to network loss

### Problem
If the device goes offline before sending a message, the message may not appear in the chat after network recovery, while chat lastMessage preview can still be updated.

### Where
ChatRepositoryImpl.sendMessage()

### Cause
Message sending is implemented as two separate Firestore writes:
1. chat document update (lastMessage fields)
2. message document creation

These operations are not atomic and are not retried when network becomes available again.

### Current behavior
- Chat preview may display a message that does not exist in messages collection
- Message is not automatically sent after network recovery

### Planned fix
- Introduce WorkManager for deferred message sending
- Use Firestore WriteBatch for atomic writes (chat + message)
- (Optional) Add local pending message state for better UX


---

## 2. Firestore schema is not centralized

### Problem
Firestore field names and collection names were previously scattered across the codebase as string literals.

### Where
Repositories and remote data access layer

### Cause
Lack of centralized constants for Firestore schema

### Current behavior
- Risk of typos (e.g. "displayNmae")
- Harder refactoring of schema changes
- Lower readability

### Planned fix
- Introduce FirestoreConstants (Collections + Fields)
- Replace all string literals with constants


---

## 3. Exception handling is not standardized

### Problem
Different parts of the data layer use generic exceptions (e.g. `Exception("...")`), which makes error handling inconsistent.

### Where
Repositories (Auth, Chat, Profile)

### Cause
No unified error model

### Current behavior
- Hard to distinguish error types
- No clear contract between layers

### Planned fix
- Replace generic exceptions with specific ones (e.g. IllegalStateException or custom exceptions)
- (Optional) Introduce sealed error model for domain layer


---

## 4. Hardcoded internal messages in data layer

### Problem
Some error/debug messages are hardcoded directly in methods.

### Where
Various places in data layer (require, throw, etc.)

### Cause
No unified approach to internal messaging

### Current behavior
- Inconsistent style across the project
- Harder to maintain and reuse messages

### Planned fix
- Extract repeated messages into constants
- Keep one-off debug messages inline where appropriate


---

## 5. ChatId normalization logic is implicit

### Problem
ChatId format depends on delimiter and ordering logic, which is not explicitly defined as a shared contract.

### Where
ChatRepository / helper methods

### Cause
Implicit string manipulation without centralized definition

### Current behavior
- Risk of inconsistent chatId generation
- Harder to reuse logic safely

### Planned fix
- Centralize chatId builder (e.g. buildChatId(uid1, uid2))
- Keep delimiter as constant
- Ensure consistent usage across the project


---

## Notes

This document tracks known limitations and planned improvements.  
Items here are intentional trade-offs made during development and are not considered blocking issues.