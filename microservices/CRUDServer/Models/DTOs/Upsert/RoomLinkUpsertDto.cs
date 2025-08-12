using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class RoomLinkUpsertDto
{
    [Required] public int StartRoomId { get; set; }
    [Required] public int EndRoomId { get; set; }
    public int Id { get; set; } // only needs to be present for update
    [Required] public string TransitionLocationDef { get; set; } = string.Empty; // for changing transitionLocationDef json blob (necessary because this is the only changeable field for now)
}
