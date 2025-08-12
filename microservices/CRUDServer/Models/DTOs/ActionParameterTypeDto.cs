namespace Uhoma.CRUDServer.Models.DTOs;

public class ActionParameterTypeDto
{
    public int HomeId { get; set; }
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string TypeDef { get; set; } = string.Empty;
}
