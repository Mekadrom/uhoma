namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class HomeUpsertDto
{
    public int Id { get; set; } // only needs to be present for update

    // if Id is present, one of these two does need to be present. if Id is absent, they both need to be present
    public string? Name { get; set; } // for changing name (not necessary)
    public string? Type { get; set; } // for changing type (not necessary)
}
