using System.Runtime.InteropServices;
using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Services;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);


builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddScoped<IActionHandlerService, ActionHandlerService>();
builder.Services.AddScoped<IActionParameterTypeService, ActionParameterTypeService>();
builder.Services.AddScoped<IHomeService, HomeService>();
builder.Services.AddScoped<IRoomService, RoomService>();
builder.Services.AddScoped<INodeService, NodeService>();
builder.Services.AddScoped<IActionService, ActionService>();
builder.Services.AddScoped<IActionParameterService, ActionParameterService>();
builder.Services.AddScoped<IRoomLinkService, RoomLinkService>();
builder.Services.AddScoped<IUserService, UserService>();

if (builder.Environment.IsDevelopment())
{
    // devonly (enables insecure logging)
    builder.Services.AddDbContext<ApplicationDbContext>(options =>
        options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
               .UseSnakeCaseNamingConvention()
               .EnableSensitiveDataLogging() // Shows parameter values
               .LogTo(Console.WriteLine, LogLevel.Information));
}
else
{
    // prod/real DB setup
    builder.Services.AddDbContext<ApplicationDbContext>(options =>
        options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
               .UseSnakeCaseNamingConvention());
}

var app = builder.Build();

// pretty neat stuff
app.UseSwagger();
app.UseSwaggerUI();

app.UseHttpsRedirection();

app.MapControllers();

// simple global exception handler to hide DB exceptions and make the user think they did something wrong
app.UseExceptionHandler(appError =>
{
    appError.Run(async context =>
    {
        var exceptionFeature = context.Features.Get<IExceptionHandlerFeature>();
        if (exceptionFeature?.Error is DbUpdateException dbEx)
        {
            context.Response.StatusCode = 400;
            context.Response.ContentType = "text/plain";
            await context.Response.WriteAsync(dbEx.InnerException?.Message ?? dbEx.Message);
        }
    });
});

app.Run();

public partial class Program { }