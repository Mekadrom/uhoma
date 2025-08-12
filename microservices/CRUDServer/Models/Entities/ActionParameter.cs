using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("action_parameter")]
[Index(nameof(ActionSeq), nameof(Name), IsUnique = true)]
public class ActionParameter
{
    [Key]
    [Column("action_parameter_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int ActionParameterSeq { get; set; }
    [Column("action_seq")]
    public int ActionSeq { get; set; }
    [Column("action_parameter_type_seq")]
    public int ActionParameterTypeSeq { get; set; }
    [Column("name")]
    public string Name { get; set; } = string.Empty;
    [Column("default_value")]
    public string DefaultValue { get; set; } = string.Empty;
}
