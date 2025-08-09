using HomeAssistantMainServer.Models.DTOs;

namespace HomeAssistantMainServer.Services;

public interface IActionService
{
    Task<IEnumerable<ActionDto>> Search(int nodeSeq, int? actionSeq, string? name);

    Task<ActionDto?> Upsert(int nodeSeq, int? actionSeq, string name);

    Task<bool> Delete(int nodeSeq, int actionSeq);
}
