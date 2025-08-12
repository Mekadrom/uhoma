using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class ActionUpsertDto
{
    [Required] public int NodeId { get; set; }
    public int Id { get; set; } // only needs to be present for update

    // if Id is present, one of these two does need to be present. if Id is absent, they both need to be present
    public int? ActionHandlerId { get; set; }
    public string? Name { get; set; } // for changing name
}
