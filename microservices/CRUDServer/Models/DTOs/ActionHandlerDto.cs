namespace Uhoma.CRUDServer.Models.DTOs;

public class ActionHandlerDto
{
    public int HomeId { get; set; }
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string HandlerDef { get; set; } = string.Empty;
}
