using System.ComponentModel.DataAnnotations;

namespace Uhoma.CRUDServer.Models.DTOs.Upsert;

public class UserUpsertDto
{
    [Required] public string Username { get; set; } = string.Empty; // necessary at all times (no user lookup by id for users), username cannot be changed
    [Required] public string Password { get; set; } = string.Empty; // for changing password or inserting new user
}
