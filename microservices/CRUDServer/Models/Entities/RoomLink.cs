using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Uhoma.CRUDServer.Models.Entities;

[Table("room_link")]
public class RoomLink
{
    [Key]
    [Column("room_link_seq")]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int RoomLinkSeq { get; set; }
    [Column("start_room_seq")]
    public int StartRoomSeq { get; set; }
    [Column("end_room_seq")]
    public int EndRoomSeq { get; set; }
    [Column("transition_location_def")]
    public string TransitionLocationDef { get; set; } = string.Empty;
}
