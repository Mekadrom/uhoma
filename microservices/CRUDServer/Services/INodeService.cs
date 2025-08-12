using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface INodeService
{
    Task<IEnumerable<NodeDto>> Search(int roomSeq, int? nodeSeq, string? name);

    Task<NodeDto?> Upsert(int roomSeq, int? nodeSeq, string name);

    Task<bool> Delete(int roomSeq, int nodeSeq);
}
