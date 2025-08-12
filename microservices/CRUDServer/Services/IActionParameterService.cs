using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IActionParameterService
{
    Task<IEnumerable<ActionParameterDto>> Search(int actionSeq, int? actionParameterSeq, string? name);

    Task<ActionParameterDto?> Upsert(int actionSeq, int? actionParameterSeq, string? name, string? defaultValue, int? actionParameterTypeSeq);

    Task<bool> Delete(int actionSeq, int actionParameterSeq);
}
