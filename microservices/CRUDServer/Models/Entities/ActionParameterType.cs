using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("action_parameter_type")]
[Index(nameof(HomeSeq), nameof(Name), IsUnique = true)]
public class ActionParameterType
{
    [Key]
    [Column("action_parameter_type_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int ActionParameterTypeSeq { get; set; }
    [Column("home_seq")]
    public int HomeSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
    [Column("type_def")]
    public string TypeDef { get; set; } = string.Empty;
}
