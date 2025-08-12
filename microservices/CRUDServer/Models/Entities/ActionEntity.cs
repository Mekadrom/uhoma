using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("action")]
[Index(nameof(NodeSeq), nameof(Name), IsUnique = true)]
public class ActionEntity
{
    [Key]
    [Column("action_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int ActionSeq { get; set; }
    [Column("node_seq")]
    public int NodeSeq { get; set; }
    [Column("action_handler_seq")]
    public int ActionHandlerSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
}
