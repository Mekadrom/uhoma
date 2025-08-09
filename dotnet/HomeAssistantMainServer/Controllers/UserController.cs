using System.ComponentModel.DataAnnotations;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.DTOs.Upsert;
using HomeAssistantMainServer.Services;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class UserController : ControllerBase
{
    private readonly ILogger<UserController> _logger;
    private readonly IUserService _userService;

    public UserController(ILogger<UserController> logger,
                          IUserService userService)
    {
        _logger = logger;
        _userService = userService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<UserDto>>> Get(int? id, string? name)
    {
        var result = await _userService.Search(id, name);
        if (result == null || (result is IEnumerable<UserDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No users found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<UserDto>> Upsert([FromBody] UserUpsertDto userDto)
    {
        return Ok(await _userService.Upsert(userDto.Username, userDto.Password));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int id)
    {
        return Ok(await _userService.Delete(id));
    }
}
