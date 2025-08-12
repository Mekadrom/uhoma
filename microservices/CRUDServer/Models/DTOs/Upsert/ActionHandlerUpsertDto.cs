using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class ActionHandlerUpsertDto
{
    [Required] public int HomeId { get; set; } // required for tenancy
    public int Id { get; set; } // only needs to be present for update

    // if id is specified, then only one of the following needs to be present. If id is not specified, both must be present.
    public string? Name { get; set; } // for changing name (not necessary)
    public string? HandlerDef { get; set; } // most common scenario is updating def
}
