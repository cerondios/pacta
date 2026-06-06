# Domain-Driven Design (DDD) — Mental Model

## What problem does it solve?

In a traditional layered architecture, business rules leak everywhere:

```java
// Service deciding domain logic from the outside
if (user.getStatus().equals("ACTIVE") &&
    user.getRoles().contains("LANDLORD") &&
    property.getOwnerId().equals(user.getId())) {
    // allow...
}
```

The service now *knows too much* about what it means to be an active landlord.
If that rule changes, you have to grep across every service that checks it.

---

## The core idea

> **The code should speak the language of the business, not the language of the database or the framework.**

Think of the domain class as a **smart object that knows its own rules**, not a dumb bag of data.

```
Bad:  service asks "what is your status?" then decides what to do
Good: service asks "can you do this?" and the object decides
```

That's the shift from **anemic domain** (getter/setter bag) to **rich domain**:

```java
// Anemic — the service decides everything
if (user.getStatus() == ACTIVE && user.getRoles().contains(LANDLORD)) { ... }

// Rich — the domain decides
if (user.canPublishProperty()) { ... }
```

---

## The three building blocks

### Entities
Have identity and a lifecycle. They change over time but stay the same *thing*.

```
User, KycDocument, ComplianceDocument
```

Two users with the same email are not the same user — identity is their `id`.

### Value Objects
No identity, defined entirely by their values. Immutable.

```
Phone, Email, Money
```

Two `Phone` objects with the same number *are* equal. There is no "which phone".

### Domain Behavior
Methods that express business rules in business language, not technical language.

```java
// Technical (anemic)
user.setStatus(UserStatus.PENDING_KYC);
user.setUpdatedAt(now());

// Business (rich)
user.verifyEmail();
```

---

## Examples from PACTA

| Instead of | Use |
|---|---|
| `user.setStatus(PENDING_KYC)` | `user.verifyEmail()` |
| `user.setStatus(BLOCKED)` | `user.block(adminId)` |
| `admin.getRole() == REVIEWER \|\| ...` | `admin.canReview()` |
| `doc.getStatus() == APPROVED && !doc.isExpired()` | `doc.isApprovedAndValid()` |
| `user.withStatus(REMOVED).withRemovalReason(...).withRemovedBy(...)` | `user.remove(reason, removedBy)` |

---

## The payoff

**When a business rule changes** — say, only `SUPER_ADMIN` can block users —
you change **one place**: `User.block()`.
Every service that calls `block()` gets the updated rule automatically.
No grep. No hunting.

**When a new developer reads** `user.block(adminId)`,
they understand the intent immediately.
When they read `user.withStatus(UserStatus.BLOCKED).withUpdatedAt(...).withUpdatedBy(...)`,
they have to reverse-engineer what's happening.

**The domain class is the documentation.**

---

## The rule of thumb

> If a service is inspecting the internals of a domain object to decide what to do,
> that logic probably belongs inside the domain object.

```java
// ❌ Service inspecting internals
if (admin.getRole() == AdminRole.REVIEWER || admin.getRole() == AdminRole.ADMIN) { ... }

// ✅ Domain owns the rule
if (admin.canReview()) { ... }
```

DDD is about making the code as close to the business conversation as possible,
so the code **becomes** the documentation.
