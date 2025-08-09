using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using HomeAssistantMainServer.Data;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Models.Entities;

[Table("home")]
[Index(nameof(OwnerUserLoginSeq), nameof(Name), IsUnique = true)]
public class Home
{
    [Key]
    [Column("home_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int HomeSeq { get; set; }
    [Column("owner_user_login_seq")]
    public int OwnerUserLoginSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
    [Column("type")]
    public string Type { get; set; } = string.Empty;

    [CreatedDate]
    [Column("created_date")]
    public DateTime? CreatedDate { get; set; }
}
