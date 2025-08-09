using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Models.Entities;

[Table("action_handler")]
[Index(nameof(HomeSeq), nameof(Name), IsUnique = true)]
public class ActionHandler
{
    [Key]
    [Column("action_handler_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int ActionHandlerSeq { get; set; }
    [Column("home_seq")]
    public int HomeSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
    [Column("handler_def")]
    public string HandlerDef { get; set; } = string.Empty;
}
