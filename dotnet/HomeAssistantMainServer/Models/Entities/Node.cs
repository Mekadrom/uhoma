using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Models.Entities;

[Table("node")]
[Index(nameof(RoomSeq), nameof(Name), IsUnique = true)]
public class Node
{
    [Key]
    [Column("node_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int NodeSeq { get; set; }
    [Column("room_seq")]
    public int RoomSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
}
