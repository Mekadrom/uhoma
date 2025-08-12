namespace Uhoma.CRUDServer.Models.DTOs;

public class NodeDto
{
    public int RoomId { get; set; }
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public IEnumerable<ActionDto> Actions { get; set; } = [];
}
