namespace HomeAssistantMainServer.Data;

[AttributeUsage(AttributeTargets.Property)]
public class CreatedDateAttribute : Attribute { }

[AttributeUsage(AttributeTargets.Property)]
public class UpdatedDateAttribute : Attribute { }
