FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY ["microservices/Microservices.sln", "./"]
COPY ["microservices/CRUDServer/CRUDServer.csproj", "CRUDServer/"]

RUN dotnet restore "CRUDServer/CRUDServer.csproj"

COPY ["microservices/CRUDServer/", "CRUDServer/"]

WORKDIR /src/CRUDServer
RUN dotnet publish -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app

RUN adduser --disabled-password --gecos '' appuser

COPY --from=build /app/publish .

USER appuser

EXPOSE 5201

ENV ASPNETCORE_URLS=http://+:5201
ENV ASPNETCORE_ENVIRONMENT=Production

ENTRYPOINT ["dotnet", "CRUDServer.dll"]
