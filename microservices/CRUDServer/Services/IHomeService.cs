using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IHomeService
{
    Task<IEnumerable<HomeDto>> Search(int userLoginSeq, int? homeSeq, string? name);

    Task<HomeDto?> Upsert(int userLoginSeq, int? homeSeq, string? name, string? type);

    Task<bool> Delete(int userLoginSeq, int homeSeq);
}
