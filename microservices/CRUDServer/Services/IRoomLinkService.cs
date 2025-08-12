using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IRoomLinkService
{
    Task<IEnumerable<RoomLinkDto>> Search(int startRoomSeq, int endRoomSeq, int? roomLinkSeq);

    Task<RoomLinkDto?> Upsert(int startRoomSeq, int endRoomSeq, int? roomLinkSeq, string transitionLocationDef);

    Task<bool> Delete(int id);
}
