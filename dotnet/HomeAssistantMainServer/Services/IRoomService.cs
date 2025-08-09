using HomeAssistantMainServer.Models.DTOs;

namespace HomeAssistantMainServer.Services;

public interface IRoomService
{
    Task<IEnumerable<RoomDto>> Search(int homeSeq, int? roomSeq, string? name);

    Task<RoomDto?> Upsert(int homeSeq, int? roomSeq, string? name);

    Task<bool> Delete(int homeSeq, int roomSeq);
}
