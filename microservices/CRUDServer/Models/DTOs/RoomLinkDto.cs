namespace Uhoma.CRUDServer.Models.DTOs;

public class RoomLinkDto
{
    public int StartRoomId { get; set; }
    public int EndRoomId { get; set; }
    public int Id { get; set; }
    public string TransitionLocationDef { get; set; } = string.Empty;
}
