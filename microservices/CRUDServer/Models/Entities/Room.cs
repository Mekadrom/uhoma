using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("room")]
[Index(nameof(HomeSeq), nameof(Name), IsUnique = true)]
public class Room
{
    [Key]
    [Column("room_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int RoomSeq { get; set; }
    [Column("home_seq")]
    public int HomeSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
}
