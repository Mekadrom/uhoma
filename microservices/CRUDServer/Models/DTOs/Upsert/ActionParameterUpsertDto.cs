using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class ActionParameterUpsertDto
{
    [Required] public int ActionId { get; set; } // required for "tenancy"
    [Required] public int ActionParameterTypeId { get; set; } // required because not null (every action parameter has a concrete type)
    public int Id { get; set; } // only needs to be present for update

    // if Id is specified, one of the following needs to be specified. If Id is not specified, they all must be present.
    public string? Name { get; set; } // for changing name (not necessary)
    public string? DefaultValue { get; set; }
    public string? CurrentValue { get; set; }
}
