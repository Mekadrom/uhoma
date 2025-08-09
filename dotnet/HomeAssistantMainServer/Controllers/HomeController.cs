using System.ComponentModel.DataAnnotations;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.DTOs.Upsert;
using HomeAssistantMainServer.Services;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class HomeController : ControllerBase
{
    private readonly ILogger<HomeController> _logger;
    private readonly IHomeService _homeService;

    public HomeController(ILogger<HomeController> logger,
                          IHomeService homeService)
    {
        _logger = logger;
        _homeService = homeService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<HomeDto>>> Get(int? id, string? name)
    {
        // todo: implement user login seq from principal token claims
        var result = await _homeService.Search(1, id, name);
        if (result == null || (result is IEnumerable<HomeDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No homes found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<HomeDto>> Upsert([FromBody] HomeUpsertDto homeDto)
    {
        return Ok(await _homeService.Upsert(1, homeDto.Id, homeDto.Name, homeDto.Type));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int id)
    {
        // todo: implement grabbing user login seq from principal
        return Ok(await _homeService.Delete(1, id));
    }
}
