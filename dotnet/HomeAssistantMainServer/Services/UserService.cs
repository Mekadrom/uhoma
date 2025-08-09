using HomeAssistantMainServer.Data;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Services;

public class UserService : IUserService
{
    private readonly ILogger<UserService> _logger;
    private readonly ApplicationDbContext _context;

    public UserService(ILogger<UserService> logger,
                       ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<UserDto>> Search(int? userLoginSeq, string? username)
    {
        return await _context.Users
            .Where(u => (userLoginSeq == null || u.UserLoginSeq == userLoginSeq)
                && (string.IsNullOrEmpty(username) || u.Username.Contains(username)))
            .Select(u => new UserDto()
            {
                Id = u.UserLoginSeq,
                Username = u.Username,
            }).ToListAsync();
    }

    public async Task<UserDto?> Upsert(string username, string password)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(h => h.Username == username);

        if (user == null)
        {
            user = new User
            {
                Username = username,
                Password = password
            };
            _context.Users.Add(user);
        }

        // todo: bcrypt
        user.Password = password;

        await _context.SaveChangesAsync();
        return new UserDto
        {
            Id = user.UserLoginSeq,
            Username = user.Username
        };
    }

    public async Task<bool> Delete(int userLoginSeq)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.UserLoginSeq == userLoginSeq);

        if (user == null)
        {
            return false;
        }

        _context.Users.Remove(user);
        await _context.SaveChangesAsync();
        return true;
    }
}
