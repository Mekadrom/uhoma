using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IActionParameterTypeService
{
    Task<IEnumerable<ActionParameterTypeDto>> Search(int homeSeq, int? actionParameterTypeSeq, string? name);

    Task<ActionParameterTypeDto?> Upsert(int homeSeq, int? actionParameterTypeSeq, string? name, string? typeDef);

    Task<bool> Delete(int homeSeq, int actionParameterTypeSeq);
}
