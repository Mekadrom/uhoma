namespace HomeAssistantMainServer.Data;

/// <summary>
/// Lets me continue using "annotations" (attributes) to automagically do some stuff that spring/hibernate/jpa supports but ASP/EFC don't.
/// Actual implementations are in ApplicationDbContext.cs
/// </summary>

[AttributeUsage(AttributeTargets.Property)]
public class CreatedDateAttribute : Attribute { }

[AttributeUsage(AttributeTargets.Property)]
public class UpdatedDateAttribute : Attribute { }

[AttributeUsage(AttributeTargets.Property)]
public class CommaSeparatedSetAttribute : Attribute { }
