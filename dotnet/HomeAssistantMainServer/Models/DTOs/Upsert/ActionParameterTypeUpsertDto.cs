using System.ComponentModel.DataAnnotations;

namespace HomeAssistantMainServer.Models.DTOs.Upsert;

public class ActionParameterTypeUpsertDto
{
    [Required] public int HomeId { get; set; } // required for tenancy
    public int Id { get; set; } // only needs to be present for update

    // if Id is specified, one of the following needs to be specified. If Id is not specified, they both must be present.
    public string? Name { get; set; } // for changing name (not necessary)
    public string? TypeDef { get; set; } // most common scenario is updating def; still not necessary if name is specified
}
