using Uhoma.CRUDServer.Models.DTOs;

namespace Uhoma.CRUDServer.Services;

public interface IUserService
{
    Task<IEnumerable<UserDto>> Search(int? userLoginSeq, string? username);

    Task<UserDto?> Upsert(string name, string password);

    Task<bool> Delete(int userLoginSeq);
}
