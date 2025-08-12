using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class NodeUpsertDto
{
    [Required] public int RoomId { get; set; }
    public int Id { get; set; } // only needs to be present for update

    // if Id is present, one of these two does need to be present. if Id is absent, they both need to be present
    [Required] public string Name { get; set; } = string.Empty; // for changing name (necessary because this is the only changeable field for now)
}
