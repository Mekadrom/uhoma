using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IActionHandlerService
{
    Task<IEnumerable<ActionHandlerDto>> Search(int homeSeq, int? actionHandlerSeq, string? name);

    Task<ActionHandlerDto?> Upsert(int homeSeq, int? actionHandlerSeq, string? name, string? handlerDef);

    Task<bool> Delete(int homeSeq, int actionHandlerSeq);
}
