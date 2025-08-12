using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Uhoma.CRUDServer.Data;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("user_login")]
[Index(nameof(Username), IsUnique = true)]
public class User
{
    [Key]
    [Column("user_login_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int UserLoginSeq { get; set; }
    [Column("username")]
    public string Username { get; set; } = string.Empty;
    [Column("password")]
    public string Password { get; set; } = string.Empty;
    [CreatedDate]
    [Column("created_date")]
    public DateTime? CreatedDate { get; set; }
    [Column("is_locked")]
    private bool IsLocked { get; set; }
    [Column("is_enabled")]
    private bool IsEnabled { get; set; }
    [Column("is_expired")]
    private bool IsExpired { get; set; }
    [Column("is_credentials_expired")]
    private bool IsCredentialsExpired { get; set; }
    [Column("roles")]
    [CommaSeparatedSet]
    private HashSet<string> Roles { get; set; } = [];
    [Column("last_login")]
    private DateTime LastLogin { get; set; }
    [Column("created")]
    private DateTime Created { get; set; }
}
