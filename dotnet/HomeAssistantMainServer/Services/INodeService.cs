using HomeAssistantMainServer.Models.DTOs;

namespace HomeAssistantMainServer.Services;

public interface INodeService
{
    Task<IEnumerable<NodeDto>> Search(int roomSeq, int? nodeSeq, string? name);

    Task<NodeDto?> Upsert(int roomSeq, int? nodeSeq, string name);

    Task<bool> Delete(int roomSeq, int nodeSeq);
}
