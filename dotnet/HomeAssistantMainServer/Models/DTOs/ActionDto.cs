namespace HomeAssistantMainServer.Models.DTOs;

public class ActionDto
{
    public int NodeId { get; set; }
    public int Id { get; set; }
    public int ActionHandlerId { get; set; }
    public string Name { get; set; } = string.Empty;
}
