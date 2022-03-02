#include "LightMap.h"

LightMap::LightMap(size_t width, size_t height)
    : width(width)
    , height(height)
    , lightData(width, std::vector<Colour>(height, Colour{ 255, 255, 255 }))
{
}

LightMap::Colour LightMap::GetLightMinusShadowAt(size_t x, size_t y, QColor shadowColor) const
{
    Colour light{ 0, 0, 0 };
    if (x > 0 && x < width && y > 0 && y < height) {
        light.red = std::clamp(lightData[x][y].red + shadowColor.red(), 0, 255);
        light.green = std::clamp(lightData[x][y].green + shadowColor.green(), 0, 255);
        light.blue = std::clamp(lightData[x][y].blue + shadowColor.blue(), 0, 255);
    }
    return light;
}

QImage LightMap::GetLightImage() const
{
    QImage lightMap(width, height, QImage::Format::Format_RGB32);

    for (size_t x = 0; x < width; ++x) {
        for (size_t y = 0; y < height; ++y) {
            int red = std::clamp(lightData[x][y].red, 0, 255);
            int green = std::clamp(lightData[x][y].green, 0, 255);
            int blue = std::clamp(lightData[x][y].blue, 0, 255);
            lightMap.setPixelColor(x, y, QColor::fromRgb(red, green, blue));
        }
    }
    return lightMap;
}

void LightMap::AddShadow(size_t shadowX, size_t shadowY, size_t shadowWidth, const QColor& shadowColour)
{
    shadowX = std::clamp(shadowX, size_t{ 0 }, width);
    shadowY = std::clamp(shadowY, size_t{ 0 }, height);
    shadowWidth = std::clamp(width - shadowX, size_t{ 0 }, shadowWidth);

    unsigned shadowEnd = shadowX + shadowWidth;
    for (size_t x = shadowX; x < shadowEnd; ++x) {
        for (size_t y = shadowY; y < height; --y) {
            lightData[x][y].red -= shadowColour.red();
            lightData[x][y].green -= shadowColour.green();
            lightData[x][y].blue -= shadowColour.blue();
        }
    }
}

void LightMap::RemoveShadow(size_t shadowX, size_t shadowY, size_t shadowWidth, const QColor& shadowColour)
{
    shadowX = std::clamp(shadowX, size_t{ 0 }, width);
    shadowY = std::clamp(shadowY, size_t{ 0 }, height);
    shadowWidth = std::clamp(width - shadowX, size_t{ 0 }, shadowWidth);

    unsigned shadowEnd = shadowX + shadowWidth;
    for (size_t x = shadowX; x < shadowEnd; ++x) {
        for (size_t y = shadowY; y < height; --y) {
            lightData[x][y].red += shadowColour.red();
            lightData[x][y].green += shadowColour.green();
            lightData[x][y].blue += shadowColour.blue();
        }
    }
}
