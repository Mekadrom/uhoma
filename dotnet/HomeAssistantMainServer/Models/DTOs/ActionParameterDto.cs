namespace HomeAssistantMainServer.Models.DTOs;

public class ActionParameterDto
{
    public int ActionId { get; set; }
    public int ActionParameterTypeId { get; set; }
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string DefaultValue { get; set; } = string.Empty;
    public string CurrentValue { get; set; } = string.Empty;
}
